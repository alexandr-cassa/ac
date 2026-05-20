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


kubectl delete -f ./g2048-deployment.yml --ignore-not-found=true
kubectl delete -f ./g2048-service.yml --ignore-not-found=true

kubectl delete -f ./postgres-deployment.yml --ignore-not-found=true
kubectl delete -f ./postgres-service.yml --ignore-not-found=true

kubectl delete -f ./redis-deployment.yml --ignore-not-found=true
kubectl delete -f ./redis-service.yml --ignore-not-found=true
