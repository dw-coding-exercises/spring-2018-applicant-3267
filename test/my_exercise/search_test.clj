(ns my-exercise.search-test
  (:require [clojure.test :refer :all]
            [my-exercise.search :refer :all]))

(deftest generate-ocd-ids-test
  (testing "empty params"
    (is (= nil (generate-ocd-ids nil))))
  (testing "state only"
    (is (= [(str ocd-id-base "/state:id")] (generate-ocd-ids {:state "ID"}))))
  (testing "state and city"
    (is (= [(str ocd-id-base "/state:id") (str ocd-id-base "/state:id/place:idaho_falls")]
           (generate-ocd-ids {:state "ID" :city "Idaho Falls"}))))
  (testing "only city"
    (is (= nil (generate-ocd-ids {:city "Boise"})))))

(deftest correctly-transform-city
  (testing "simple"
    (is (= "boise" (transform-city "Boise"))))
  (testing "with spaces"
    (is (= "new_york" (transform-city "New York")))))