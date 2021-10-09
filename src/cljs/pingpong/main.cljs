(ns pingpong.main
  (:require [reagent.core :as r]
            [ajax.core :as ajax]
            [reagent.dom :as dom]
            [re-frame.core :as rf]
            ;; requiring for side-effects
            [day8.re-frame.http-fx]))


(rf/reg-event-db
 ::pong
 (fn [db [_ result]]
   (assoc db ::pong result)))

(rf/reg-event-db
 ::error
 (fn [db [_ result]]
   (assoc db ::error result)))

(defn ping
  "Ping the API, dispatching `::pong` on success or `::error` on failure."
  [cofx]
  {:db (dissoc (:db cofx) ::pong ::error)
   :http-xhrio {:method :get
                :uri "http://localhost:9001/api/ping"
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [::pong]
                :on-failure [::error]}})

(rf/reg-event-fx ::ping ping)

(rf/reg-sub
 ::pong
 (fn [db _query]
   (get-in db [::pong :ping])))

(rf/reg-sub
 ::error
 (fn [db _query]
   (get-in db [::error :status-text])))

(defn ^:dev/after-load clear-subs!
  "Clear subscription cache after a hot reload. Otherwise changing subscription
  definitions would do nothing."
  []
  (rf/clear-subscription-cache!))

(defn <root>
  []
  (r/with-let [pong (rf/subscribe [::pong])
               error (rf/subscribe [::error])]
    [:div
     [:button {:on-click #(rf/dispatch [::ping])} "Ping!"]
     (when @pong
       [:div @pong])
     (when @error
       [:div {:style {:color "red"}} "Unable to ping: " @error])]))

(defn ^:dev/after-load main
  []
  (dom/render [<root>] (.getElementById js/document "root")))
