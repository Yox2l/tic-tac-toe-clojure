(defproject lambda-serverless "0.1.0"
  :description "simple tic tac toe lambda function"
  :url "https://route42.co.il/clojure-part-1-logic/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/faraday "1.11.4"]
                 [org.clojure/data.json "0.2.6"]
                 [uswitch/lambada "0.1.2"]]
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "lambda-serverless.jar")
