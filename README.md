# Streamvisor Wrapped - Queryable Pulsar Topics using TableView and Spring

This repository contains the source code for our [blogpost](https://www.streamvisor.com/blog/streamvisor-wrapped-queryable-pulsar-topics-using-tableview-and-spring).

It provides an example on how to create realtime music streaming statistic application using [TableView](https://pulsar.apache.org/docs/3.1.x/concepts-clients/#tableview) and [Spring for Apache Pulsar](https://docs.spring.io/spring-pulsar/reference/).

## Getting started

Make sure to have Docker installed and running.

Using Springs Docker Compose support, the application will start Pulsar standalone and Streamvisor Community docker containers, so you can run a self-contained example.

To start the application simply run:
```sh
./gradlew bootRun
```

## Explore the topology
After the application is running, you can explore the topology and take a look at its messages.  
To do so, open the following URL in your browser:
```
http://localhost:8888/
```
and log in with these credentials:
```
username: admin
password: streamvisor
```
Select the `standalone` environment if necessary, and head to the `public/default` namespace in the Explorer.

Exploring topologies:

<img alt="topology explorer" src=img/topology-explorer.png width="500"/>


Browsing through topic messages:

<img alt="topic browser" src=img/topic-browser.png width="500"/>

## Query the REST-API
To query the REST-API, open another terminal tab and use `curl` to issue requests:

```sh
curl localhost:8080/api/stats/0/wrapped

{
  "topArtists": [
    "Electric Guest",
    "KAYTRANADA",
    "Anderson .Paak",
    "Tennis",
    "Bear Hands"
  ],
  "topTracks": [
    "Play With Me",
    "Hurt Feelings",
    "Go DJ",
    "Birthday",
    "Question"
  ],
  "totalPlays": 1064
}
```


The following `GET` endpoints are available:

| Endpoint                                       | Description                                                                     |
|------------------------------------------------|---------------------------------------------------------------------------------|
| `/api/stats/{userId}/wrapped`                  | Returns a Spotify Wrapped style statistic about the users listening preferences |
| `/api/stats/{userId}/plays`                    | Returns the total number of streams a user has played                           |
| `/api/stats/{userId}/artists/top`              | Returns the top 5 artists a user has listened to                                |
| `/api/stats/{userId}/artists/{artistId}/plays` | Returns the number of times a user has listened to an artist                    |
| `/api/stats/{userId}/tracks/top`               | Returns the top 5 tracks a user has listened to                                 | 
| `/api/stats/{userId}/tracks/{trackId}/plays`   | Returns the number of times a user has listened to a track                      |
