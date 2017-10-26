(ns recipes.server-query-security-client
  (:require
    [fulcro.client.core :as fc]
    [fulcro.client.data-fetch :as df]
    [fulcro.client.mutations :as m]
    [fulcro.client.dom :as dom]
    [fulcro.client.primitives :as om :refer [defui]]))

(def initial-state {:ui/react-key "abc"})

(defonce app (atom (fc/new-fulcro-client
                     :initial-state initial-state
                     :started-callback
                     (fn [{:keys [reconciler]}]
                       ; TODO
                       ))))

(defui ^:once Person
  static om/IQuery
  (query [this] [:ui/fetch-state :name :address :cc-number])
  Object
  (render [this]
    (let [{:keys [name address cc-number]} (om/props this)]
      (dom/div nil
        (dom/ul nil
          (dom/li nil (str "name: " name))
          (dom/li nil (str "address: " address))
          (dom/li nil (str "cc-number: " cc-number)))))))

(def ui-person (om/factory Person))

(defui ^:once Root
  static om/IQuery
  (query [this] [:ui/react-key {:person (om/get-query Person)} :fulcro/server-error])
  Object
  (render [this]
    (let [{:keys [ui/react-key person server-error] :or {ui/react-key "ROOT"} :as props} (om/props this)]
      (dom/div #js {:key react-key}
        (when server-error
          (dom/p nil (pr-str "SERVER ERROR: " server-error)))
        (dom/button #js {:onClick #(df/load this :person Person {:refresh [:person]})} "Query for person with credit card")
        (dom/button #js {:onClick #(df/load this :person Person {:refresh [:person] :without #{:cc-number}})} "Query for person WITHOUT credit card")
        (df/lazily-loaded ui-person person)))))
