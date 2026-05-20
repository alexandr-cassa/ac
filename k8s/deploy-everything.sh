#!/usr/bin/env bash
set -euo pipefail

# MINIKUBE CHECKS
echo "Checking Minikube status..."
if ! command -v minikube >/dev/null 2>&1; then
  echo "Error: minikube is not installed or not in PATH"
  exit 1
fi

if ! minikube status >/dev/null 2>&1; then
  echo "Minikube is not running. Starting Minikube..."
  minikube start
  echo "Minikube is ready."
else
  echo "Minikube is already running."
fi

# KUBECTL CHECKS
echo "Checking kubectl status..."
if ! command -v kubectl >/dev/null 2>&1; then
  echo "Error: kubectl is not installed or not in PATH"
  exit 1
fi

kubectl apply -f ./postgres-pvc.yml
kubectl apply -f ./postgres-deployment.yml
kubectl apply -f ./postgres-service.yml

kubectl apply -f ./redis-deployment.yml
kubectl apply -f ./redis-service.yml

echo "Waiting for postgres to be ready"
kubectl wait --for=condition=available deployment/postgres --timeout=30s
echo "Waiting for redis to be ready"
kubectl wait --for=condition=available deployment/redis --timeout=30s

kubectl apply -f ./g2048-deployment.yml
kubectl apply -f ./g2048-service.yml

echo "Waiting for backend to be ready"
kubectl wait --for=condition=available deployment/g2048 --timeout=30s

minikube service g2048 --url