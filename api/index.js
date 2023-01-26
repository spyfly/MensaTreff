/* OpenAPI Spec Start */
const swaggerJsdoc = require('swagger-jsdoc');


const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'MensaTreff API',
      version: '0.0.1',
    },
  },
  apis: ['./routes/*.js'], // files containing annotations as above
};

const openapiSpecification = swaggerJsdoc(options);
/* OpenAPI Spec Finish */

const express = require('express');
const bodyParser = require('body-parser')
const app = express();
app.use(bodyParser.urlencoded({ extended: false }))

const swaggerUi = require('swagger-ui-express');

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(openapiSpecification));

/* DB Setup */
const sqlite3 = require('sqlite3');
const { open } = require('sqlite');
(async () => {
  const db = await open({
    filename: 'db.sqlite3',
    driver: sqlite3.Database
  })

  await db.run(`CREATE TABLE IF NOT EXISTS users (
	  id INTEGER PRIMARY KEY AUTOINCREMENT,
   	username TEXT NOT NULL,
	  passkey TEXT NOT NULL
)`)

  await db.run(`CREATE TABLE IF NOT EXISTS matches (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  mensaId INTEGER NOT NULL,
  date TEXT NOT NULL,
  timeslot TEXT NOT NULL,
  userId TEXT NOT NULL
)`)

  const mensas = require('./routes/mensas.js');
  mensas.setup(app);
  const match = require('./routes/match.js');
  match.setup(app, db);
  const user = require('./routes/user.js')
  user.setup(app, db);

  const port = 3000;
  app.listen(port, () => {
    console.log(`MensaTreff API listening on port ${port}`)
  })
})()
