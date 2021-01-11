# rasa-java-action-service

A Java based [Rasa Action Server](https://rasa.com/docs/action-server/)

The **rasa-java-action-service** is an environment for building your [Rasa Custom Actions](https://rasa.com/docs/rasa/custom-actions) with Java. 

- Build your Rasa assistant fulfillment easily: your Actions just implement a simple Java interface
- With full [Rasa](https://rasa.com/docs/rasa/) stack onboard: set up Rasa and connect to your Action Server with
  only one command
- Control the Rasa CLI with simple HTTP API calls

The **rasa-java-action-service** is inspired by [Rafał Bajek's Action Server implementation](https://github.com/rbajek/rasa-java-action-server)
and uses his [SDK](https://github.com/rbajek/rasa-java-sdk)

### Version

`current version: 0.2.0` (unstable)

## Installation
The **rasa-java-action-service** is not a 'ready to use' application. It functions as a template and starting point to build
your Rasa assistant's fulfillment. Therefore, just clone this repo and start.

You need to [install Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/).


## Use Case

Use the **rasa-java-action-service** if you consider building your Rasa Action Server with Java.

## Background

Almost every advanced, business integrated conversational AI system has the ability to perform business logic procedures
during a conversation with the user - be it a calculation, a database query or an API call.
This ability is called _fulfillment_ and is typically organized in so-called _Actions_. An
Action is a part of code which is responsible for one independent business logic procedure. 

In [Rasa](https://rasa.com/docs/rasa/), Actions are placed within the _Rasa Action Server_. If Rasa predicts an Action to be
executed, it calls the Action Server which executes the Action thereupon and responds with an enriched response object to the Rasa Server.

## Usage

The **rasa-java-action-service** provides an initial set-up configuration which makes a quick development start possible.

### Start
- clone this repo
- go to project directory 
- in the command line type `docker-compose -f docker-compose.develop.yml up -d`
  - after an image build this will run a docker container named `rasa_server_dev` starting the Rasa Server with API enabled listening on `localhost:5005`
  - the `rasabot` folder is mounted in the container's `/app` directory
- the `rasabot` folder contains an initial set of files which you can use as a starting point to [build your Rasa assistant](https://rasa.com/docs/rasa/playground)
- type now `docker exec -it rasa_server_dev bash` to get the container shell
- type `rasa train` into the container shell
  - this will train a Rasa model based on the content of the `.yml` files in the `rasabot` folder
  - the `.yml` files are initially prefilled with content which defines a sample Rasa bot
  - the sample bot is an extended Rasa 'mood bot' which asks you how you are, and tries to cheer you up if you feel bad. 
- start the `RasaActionServer` (within IDE or after a build with`java -jar`). The Action Server listens per default on port `5055`, see `application.properties` 
  - initially the `RasaActionServer` contains an sample Action named `ActionSample` which retrieves a joke though an API call.
  - Rasa tries to predict this Action and calls the Action Server if you tell the bot that you feel bad
- after training completed, type `rasa shell -p 5006` to load the model. After loading start the conversation by greeting the bot. The bot will ask you, how you are.
- if you tell the bot you feel sad, the bot will try to cheer you up with a joke and call the Action Server.
- if the bot responds with a joke, then the Action Server connection works, and your set-up was successful.
  
### Development

See the [Rasa Docs](https://rasa.com/docs/rasa/) if you want to know how to build conversational assistants with Rasa.

Build your Actions with the **rasa-java-action-service** by just implementing the `Action` interface and declaring them with the Spring 
`@Component` annotation:

```java
@Component
public class ActionSample implements Action {
    
    @Override
    public String name() {
        return "<name of you Action as defined in domain.yml file>";
    }

    @Override
    public List<AbstractEvent> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) {
        
        List<AbstractEvent> eventList = null;
        //your Custom Action code
        //...
        
        //provide your responses
        dispatcher.utterMessage("<your message>");
        
        //provide events
        return eventList;
    }

}
```
See also the sample Action `ActionSample` in the `actions` package.

For local development just edit the `.yml` files in the `rasabot` folder.
Afterwards you need to re-train your bot. See [start section](#Start) how to train the bot and start a conversation in the Rasa Shell.

### Deployment

If your Actions are implemented, then your Action Server is ready for deployment.
You can deploy your Action Server everywhere where you have Docker and Docker Compose installed and access to your project.

- uncomment the `http://actionservice:5055/webhook` url in `action_endpoint` section of the `endpoints.yml` file. Comment the other urls in the `action_endpoint` section.
- go to the project directory
- type in the command line: `docker-compose up -d`. This will result in the following steps:
    - a multi-stage image build for the Action Server will be triggered and the `action_server` container will be started listening on exposed port `5055`
    - a multi-stage image build for an extended Rasa environment will be triggered and the `rasa_server` container will be started listening on exposed port `5005`
    - within the `rasa_server` container a flask application will be started listening on exposed port `4000`. Attention! This is an experimental feature! It is
used to access the Rasa CLI through an API endpoint.
    - `action_server` and `rasa_server` are connected with a shared named volume. You can see the same content in the `/app` directory of `rasa_server`
       as well as in the `/app/rasabot` directory of `action_server`
      - the shared volume is used because Rasa doesn't provide any API endpoints to manipulate the `endpoints.yml` and the `credentials.yml` files. With the shared volume configuration
your Action Server can be extended with corresponding API endpoints to solve this problem.
        
### After Deployment

After your Action Server and Rasa are deployed, you can talk with or train your Rasa bot either in the Rasa Shell as shown in the [start section](#Start)
or using the [Rasa Open Source HTTP API](https://rasa.com/docs/action-server/about-http-api).

If you want to build your Rasa assistant with **Java** you should use the [rasa-java-client-library](https://github.com/ArturKorb/rasa-java-client-library).
- Use the comfortable `RasaClient` to connect to the Rasa Server and talk to or train your Rasa assistant
- Use the flexible `ModelApi`, `DomainApi` and `TrackerApi` to implement additional advanced features,
  e.g. [interactive learning](https://rasa.com/docs/rasa/writing-stories#using-interactive-learning)
  
## Experimental

Within the `rasa_server` container a flask application will be started listening on exposed port `4000`. Attention! This is an experimental feature!
The flask server provides an endpoint called `http://localhost:4000/commands/rasa` and can be used to control Rasa by requesting 
[Rasa CLI](https://rasa.com/docs/rasa/command-line-interface) commands. Interactive commands like `rasa shell` or `rasa interactive` won't work.

### Usage

A request like this:

```shell
curl -X POST -H 'Content-Type: application/json' -d '{"args": ["train", "--num-threads", "4"]}' http://localhost:4000/commands/rasa
```
... will return a JSON response like this:

```json
{
   "key": "123456",
   "result_url": "http://localhost:4000/commands/rasa?key=123456",
   "status": "running"
}
```
now you can poll on the delivered key:

```shell
curl http://localhost:4000/commands/rasa?key=123456
```

... to get the result when the command returns:

```json
{
  "report": "<rasa train output>",
  "key": "123456",
  "start_time": 1593019807.7754705,
  "end_time": 7593019807.782958,
  "process_time": 6.00748753547668457,
  "returncode": 0,
  "error": null
}
```


