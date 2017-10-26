(ns fulcro.ui.elements
  (:require [fulcro.client.primitives :as prim :refer [defui]]
            [fulcro.client.dom :as dom]))

(defn react-instance?
  "Returns the react-instance (which is logically true) iff the given react instance is an instance of the given react class.
  Otherwise returns nil."
  [react-class react-element]
  {:pre [react-class react-element]}
  ; TODO: this isn't quite right
  (when (= (prim/react-type react-element) react-class)
    react-element))

(defn first-node-of-type
  "Finds (and returns) the first child that is an instance of the given React class (or nil if not found)."
  [react-class sequence-of-react-instances]
  (some #(react-instance? react-class %) sequence-of-react-instances))

#?(:cljs
   (defn update-frame-content [this child]
     (let [frame-component (prim/get-state this :frame-component)]
       (when frame-component
         (js/ReactDOM.render child frame-component)))))

#?(:cljs
   (defui IFrame
     Object
     (initLocalState [this] {:border 0})
     (componentDidMount [this]
       (let [frame-body (.-body (.-contentDocument (js/ReactDOM.findDOMNode this)))
             child      (:child (prim/props this))
             e1         (.createElement js/document "div")]
         (.appendChild frame-body e1)
         (prim/update-state! this assoc :frame-component e1)
         (update-frame-content this child)))
     (componentDidUpdate [this pprops pstate]
       (let [child (:child (prim/props this))]
         (update-frame-content this child)))
     (render [this]
       (dom/iframe (-> (prim/props this) (dissoc :child) clj->js)))))

#?(:cljs
   (defn ui-iframe [props child]
     ((prim/factory IFrame) (assoc props :child child))))


