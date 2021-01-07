from flask import Flask
from flask_executor import Executor
from flask_shell2http import Shell2HTTP

app = Flask(__name__)
executor = Executor(app)
shell2http = Shell2HTTP(app=app, executor=executor, base_url_prefix="/commands/")

shell2http.register_command(endpoint="saythis", command_name="echo")
shell2http.register_command(endpoint="rasa", command_name="rasa")
# shell2http.register_command(endpoint="bash", command_name="bash")

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=4000)
