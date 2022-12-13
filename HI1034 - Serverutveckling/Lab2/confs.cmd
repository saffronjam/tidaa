docker build --no-cache -t canvas-ms-image ./canvas-ms
docker build --no-cache -t chat-ms-image ./ChatMS
docker build --no-cache -t image-ms-image ./ImageMS
docker build --no-cache -t post-ms-image ./PostMS
docker build --no-cache -t user-ms-image ./UserMS
docker build --no-cache -t vertx-ms-image ./VertxMS
docker build --no-cache -t frontend-image ./frontend

kubectl apply -f confs

kubectl expose deployment canvas-ms --type=LoadBalancer --port=5000
kubectl expose deployment chat-ms --type=LoadBalancer --port=8081
kubectl expose deployment image-ms --type=LoadBalancer --port=8082
kubectl expose deployment post-ms --type=LoadBalancer --port=8083
kubectl expose deployment user-ms --type=LoadBalancer --port=8084
kubectl expose deployment vertx-ms --type=LoadBalancer --port=8086
kubectl expose deployment frontend --type=LoadBalancer --port=3000

minikube tunnel