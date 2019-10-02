#!/usr/bin/env bash
eval $(minikube docker-env)
kubectl apply -f 1_namespaces.yaml --force
kubectl replace -f 2_services.yaml --force
kubectl replace -f 3_routing.yaml --force

kubectl label namespace app istio-injection=enabled --overwrite

mvn clean install dockerfile:build -Pdocker -f ../user/user-micronaut-app/pom.xml
mvn clean install dockerfile:build -Pdocker -f ../product/product-micronaut-app/pom.xml
mvn clean install dockerfile:build -Pdocker -f ../offer/offer-spring-app/pom.xml
mvn clean install dockerfile:build -Pdocker -f ../instalment/instalment-spring-app/pom.xml
mvn clean install dockerfile:build -Pdocker -f ../loan/loan-spring-app/pom.xml

docker build -t javorex/offer-frontend ../offer/offer-frontend/
docker build -t javorex/loan-frontend ../loan/loan-frontend/

kubectl replace -f 4_deploy.yaml --force
