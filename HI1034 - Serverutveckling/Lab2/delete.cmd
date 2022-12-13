kubectl delete -n default deployment canvas-ms
kubectl delete -n default deployment chat-ms
kubectl delete -n default deployment image-ms
kubectl delete -n default deployment post-ms
kubectl delete -n default deployment user-ms
kubectl delete -n default deployment vertx-ms
kubectl delete -n default deployment frontend

kubectl delete -n default deployment canvas-db
kubectl delete -n default deployment chat-db
kubectl delete -n default deployment image-db
kubectl delete -n default deployment post-db
kubectl delete -n default deployment user-db
kubectl delete -n default deployment vertx-db

kubectl delete -n default persistentvolumeclaim canvas-db-pv-claim
kubectl delete -n default persistentvolumeclaim chat-db-pv-claim
kubectl delete -n default persistentvolumeclaim image-db-pv-claim
kubectl delete -n default persistentvolumeclaim post-db-pv-claim
kubectl delete -n default persistentvolumeclaim user-db-pv-claim
kubectl delete -n default persistentvolumeclaim vertx-db-pv-claim

kubectl delete -n default service canvas-db
kubectl delete -n default service chat-db
kubectl delete -n default service image-db
kubectl delete -n default service post-db
kubectl delete -n default service user-db
kubectl delete -n default service vertx-db

kubectl delete -n default service canvas-ms
kubectl delete -n default service chat-ms
kubectl delete -n default service image-ms
kubectl delete -n default service post-ms
kubectl delete -n default service user-ms
kubectl delete -n default service vertx-ms
kubectl delete -n default service frontend

