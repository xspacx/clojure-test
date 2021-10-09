(ns pingpong.core-test
  (:require [clojure.test :refer [deftest is]]
            [ring.util.http-response :as response]
            [pingpong.core :as sut]))

(deftest pong-response
  (let [res (sut/pong {})]
    (is (= (response/ok {:ping "pong"}) res)
        "it should pong")))
