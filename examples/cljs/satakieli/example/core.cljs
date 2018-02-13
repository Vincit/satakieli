(ns satakieli.example.core
  (:require [satakieli.format :as sf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.string :as str]
            [reagent.ratom :as a]
            [satakieli.example.translations :refer [translations]]))

(enable-console-print!)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(reset! re-frame.db/app-db
        {:active-lang "fi"
         :name        "Peter"
         :candy-count 0})
;register subs and event handlers for first level items
(run!
  (fn [[k _]]
    (rf/reg-sub k (fn [db _] (db k)))
    (rf/reg-event-db (keyword (str "set-" (name k)))
                     (fn [db [_ v]]
                       (assoc db k v))))
  @re-frame.db/app-db)


(def translate
  (let [active-lang (rf/subscribe [:active-lang])]
    (fn
      ([ks-str args]
       (sf/translate translations (concat [@active-lang] (str/split ks-str #"\.")) args))
      ([ks-str]
       (translate ks-str {})))))

(defn lang-buttons [active-lang]
  [:div
   (->> (keys translations)
        (map
          (fn [l]
            [:button.lang-button {:key l :on-click #(rf/dispatch [:set-active-lang l])} (name l)]))
        (doall))])

(defn- set-now [now & _]
  (reset! now (new js/Date)))

(defn helloapp []
  (let [lang        (rf/subscribe [:active-lang])
        name        (rf/subscribe [:name])
        candy-count (rf/subscribe [:candy-count])
        now         (a/atom (new js/Date))
        interval    (atom nil)]
    (r/create-class
      {:component-did-mount    #(reset! interval (.setInterval js/window (partial set-now now) 1000))
       :component-will-unmount #(.clearInterval js/window @interval)
       :reagent-render
                               (fn []
                                 [:div
                                  [:div (translate "some.deep.path.key")]
                                  [:div (translate "hello")]
                                  [:div (translate "name" {:name @name})]
                                  [:div (translate "time" {:now @now})]
                                  [:div (translate "candy" {:count @candy-count})]
                                  [:input {:type "text" :value @name :on-change #(rf/dispatch-sync [:set-name (-> % .-target .-value)])}]
                                  [:input {:type "number" :value @candy-count :on-change #(rf/dispatch [:set-candy-count (-> % .-target .-value)])}]
                                  [lang-buttons @lang]])})))

(r/render [helloapp] (.getElementById js/document "app"))

(comment
  translations)