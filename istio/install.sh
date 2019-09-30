#!/usr/bin/env bash
INSTALL_DIR=/opt/
ISTIO_VER="1.2.2"

cd ${INSTALL_DIR}

helm delete --purge istio
helm delete --purge istio-init
kubectl delete namespace istio-system

cd istio-${ISTIO_VER}

kubectl create namespace istio-system
helm install install/kubernetes/helm/istio-init --name istio-init --namespace istio-system

echo "Waiting for CRDs..."
until  [ $(kubectl get crds | grep -c -s 'istio.io\|certmanager.k8s.io') = "23" ] || [ $(kubectl get crds | grep -c -s 'istio.io\|certmanager.k8s.io') = "28" ];
do
  echo "Waiting..."
  sleep 5s
done

helm install install/kubernetes/helm/istio \
  --name istio \
  --namespace istio-system \
  --set global.disablePolicyChecks=false \
  --set grafana.enabled=true \
  --set servicegraph.enabled=true \
  --set tracing.enabled=true \
  --set pilot.traceSampling=100.0