FROM amazoncorretto:17-al2023

ENV APP_NAME mongo-fle

COPY ./target/${APP_NAME}*.jar /app/${APP_NAME}.jar
COPY ./docker-entrypoint.sh /app/docker-entrypoint.sh
COPY ./mongo_crypt_shared_v1-linux-aarch64-enterprise-amazon2023-7.0.5/lib/ /app/mongo_crypt/

WORKDIR /app

ENTRYPOINT [ "./docker-entrypoint.sh" ]