#!/bin/bash

setVariables() {

    NAME="codespaces-tracker"
    NAMESPACE="codespaces-tracker"

    VERSION=1.03
    IMAGE="shinojosa/$NAME:$VERSION"

    DEPLOYMENT=$NAME
    CONTAINER=$IMAGE
    YAMLFILE=$VERSION-$(date '+%Y-%m-%d_%H_%M_%S').yaml
    export RELEASE_VERSION=$VERSION
    export IMAGE=$IMAGE

}

crossCompilePushDockerImage() {
    #clean before building
    mvn clean package
    #build the image
    docker buildx build --platform linux/amd64,linux/arm64 --push --tag $IMAGE .
}


createDeployment() {

    envsubst <k8s/deployment.yaml >k8s/gen/deploy-$YAMLFILE

    kubectl apply -f k8s/gen/deploy-$YAMLFILE
    # kubectl set image deployment/$deployment $name=$container -n $ns
}


setVariables
crossCompilePushDockerImage
createDeployment
