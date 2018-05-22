(ns my-exercise.search
  (:require [clojure.pprint :as pp]
            [clj-http.client :as client]
            [clojure.string :as string]
            [json-html.core :as json-html]
            [hiccup.page :refer [html5]]
            [clojure.edn :as edn]
            [clojure.walk :as walk]))

(def democracy-now-endpoint
  "Takes a comma seperated list of ocd-ids as parameter.
  e.g. ocd-division/country:us/state:nj,ocd-division/country:us/state:nj/place:newark"
  "https://api.turbovote.org/elections/upcoming?district-divisions=")

(def ocd-id-base
  "Assume country is United States"
  "ocd-division/country:us")

(defn transform-city
  "Takes a city name. Lower cases and replaces spaces with underscores."
  [city]
  (-> city
      string/lower-case
      (string/replace #"\s" "_")))

(defn generate-ocd-ids
  "Generates a list of ocd-ids"
  [{:keys [street street-2 city state zip]}]
  (let [state-ocd-id (string/lower-case (str ocd-id-base "/state:" state))]
    (not-empty
      (cond-> []
              (not-empty state) (conj state-ocd-id)
              (and (not-empty state) (not-empty city)) (conj (str state-ocd-id "/place:" (transform-city city)))))))

(defn fetch-ocd-ids
  [ocd-ids]
  (->> (client/get (str democracy-now-endpoint (string/join "," ocd-ids))
                   {:insecure? true})
       :body
       edn/read-string))

(defn search [{:keys [form-params] :as request}]
  (html5 [:head [:style (-> "json.human.css" clojure.java.io/resource slurp)]]
         (json-html/edn->html
           (some-> form-params
                   walk/keywordize-keys
                   generate-ocd-ids
                   fetch-ocd-ids))))
