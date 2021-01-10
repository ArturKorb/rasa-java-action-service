# rasa-java-action-service

A Java based [Rasa Action Server](https://rasa.com/docs/action-server/)

The **rasa-java-action-service** is an environment for building your [Rasa Custom Actions](https://rasa.com/docs/rasa/custom-actions) with Java. 

- Build your Rasa assistant fulfillment easily: your Actions just implement a simple Java interface
- With full [Rasa](https://rasa.com/docs/rasa/) stack onboard: set up Rasa and connect to your Action Server with
  only one command
- Control Rasa's CLI with simple HTTP API calls

The **rasa-java-action-service** is inspired by [Rafał Bajek's Action Server implementation](https://github.com/rbajek/rasa-java-action-server)
and uses his [SDK](https://github.com/rbajek/rasa-java-sdk)

### Version

`current version: 0.2.0` (unstable)

## Installation
The **rasa-java-action-service** isn't a 'ready to use' application. It functions as a template and starting point to build
your Rasa chatbot's or assistant's fulfillment. Therefore, just clone this repo and start.

You need to [install Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/).


## Use Case

Use the **rasa-java-action-service** if you consider building your Rasa Action Server with Java.

## Background

Almost every more complex, business integrated conversational AI system has the ability to perform business logic procedures
within a conversation with the user. That could be some calculation of data or data retrieval through an API call or from
a database. This ability is called 'fulfillment' and is typically organized in so-called 'Actions'. An
Action is code which is responsible for one independent business logic procedure. 

In [Rasa](https://rasa.com/docs/rasa/), Actions are placed within the Rasa Action Server. If Rasa predicts an Action to be
executed, it calls the Action Server which executes the Action and sends back an enriched response object to Rasa.

## Usage

The **rasa-java-action-service** comes with an initial configuration of all components to make a quick development start possible.

### Start
- clone this repo
- go to project directory 
- in the command line type `docker-compose -f docker-compose.develop.yml up -d`
  - after an image build this will run a docker container named `rasa_server_dev` starting the Rasa Server with API enabled listening on `localhost:5005`
  - the `rasabot` folder is mounted within the container's `/app` directory
- the `rasabot` folder contains an initial set of files which you can use as starting point to [build your Rasa Assistant](https://rasa.com/docs/rasa/playground)
- type now `docker exec -it rasa_server_dev bash` to get the shell of the container
- type `rasa train` in the container shell
  - this will train a Rasa model based on the content of the `.yml` files in the `rasabot` folder
  - the `.yml` files are initially prefilled with content for a sample bot 
  - the sample bot is an extended Rasa 'mood bot' which asks how you are and tries to cheer you up if you feel bad. 
- start the `RasaActionServer` (within IDE or after build with`java -jar`). The Action Server listens per default on port `5055`, see `application.properties`
  - initially the `RasaActionServer` contains the sample Action `ActionSample` which retrieves a joke though an API call
  - Rasa tries to predict this Action and call the Action Server if you feel sad
- after training completed, type `rasa shell -p 5006` to load the model and talk to the bot. After greeting the bot will ask you, how you are.
- if you tell the bot you feel sad, the bot will try to cheer you up with a joke and call the Action Server
- if the bot responds with a joke, then the Action Server connection works, and your set-up is successful
  
### Development

See the [Rasa Docs](https://rasa.com/docs/rasa/) if you want to know how to build Conversational Assistants with Rasa.

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
        dispatcher.utterMessage(joke);
        
        //provide events
        return eventList;
    }

}
```
See also the sample Action `ActionSample` in the `actions` package.

- for local development just edit the `.yml` files in the `rasabot` folder.
- afterwards you need to re-train your bot. See [start section](#Start) how to train the bot and start a conversation within the Rasa Shell.

### Deployment

If your Actions are implemented, your Action Server is ready for deployment.
You can deploy your Action Server everywhere where you have Docker and Docker Compose installed and access to your project

- uncomment in the `endpoints.yml` the `http://actionservice:5055/webhook` url in `action_endpoint` section. Comment the other urls in the `action_endpoint` section.
- go to the project directory
- type in the command line: `docker-compose up -d`. This will result in the following steps:
    - a multi-stage image build for the Action Server will be triggered and the `action_server` container will be started listening on exposed port `5055`
    - a multi-stage image build for an extended Rasa environment will be triggered and the `rasa_server` container will be started listening on exposed port `5005`
    - within the `rasa_server` container a flask application will be started listening on exposed port `4000`. Attention! This is only experimental 
      and can be used to control the Rasa CLI through an API endpoint.
    - `action_server` and `rasa_server` are connected with a shared named volume. You see the same contents in `/app` directory of `rasa_server`
       and `/app/rasabot` directory of `action_server`
      - the shared volume is used because Rasa doesn't provide API endpoints to manipulate the `endpoints.yml` and the `credentials.yml`. With the shared volume configuration
        your Action Server can be extended with corresponding API endpoints to solve this problem.
        
### After Deployment

After your Action Server and Rasa are deployed, you can talk with or train your Rasa bot either in the Rasa Shell as shown in the [start section](#Start)
or using the [Rasa Open Source HTTP API](https://rasa.com/docs/action-server/about-http-api).

If you want to build your Rasa assistant with **Java** you can use the [rasa-java-client-library](https://github.com/ArturKorb/rasa-java-client-library).
- Use the comfortable `RasaClient` to connect to the Rasa Server and talk to or train your Rasa assistant
- Use the flexible `ModelApi`, `DomainApi` and `TrackerApi` to implement additional advanced features,
  e.g. [interactive learning](https://rasa.com/docs/rasa/writing-stories#using-interactive-learning)
  
## Experimental

Within the `rasa_server` container a flask application will be started listening on exposed port `4000`. Attention! This is only experimental!
The flask server provides an Endpoint called `http://localhost:4000/commands/rasa` and can be used to control Rasa by requesting 
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
  "end_time": 1593019807.782958,
  "process_time": 6.00748753547668457,
  "returncode": 0,
  "error": null
}
```


