/* OpenAPI Spec Start */
const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Hello World',
      version: '1.0.0',
    },
  },
  apis: ['./index.js'], // files containing annotations as above
};

const openapiSpecification = swaggerJsdoc(options);
/* OpenAPI Spec Finish */

const express = require('express');
const app = express();
const axios = require('axios');

const swaggerUi = require('swagger-ui-express');

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(openapiSpecification));

/**
 * @openapi
 * /mensas:
 *   get:
 *     description: Get all Mensas and opening days
 *     responses:
 *       200:
 *         description: List of Mensas with opening days
 */
app.get('/mensas', (req, res) => {
  axios.get('https://api.studentenwerk-dresden.de/openmensa/v2/canteens')
  .then(async function (response) {
    var mensas = [];
    for (const mensa of response.data) {
      const mensaObj = mensa;
      const openingDays = await axios.get('https://api.studentenwerk-dresden.de/openmensa/v2/canteens/'+mensa.id+'/days');
      mensaObj.days = openingDays.data;
      mensas.push(mensaObj);
    }
    res.send({
      response: mensas,
    })
  })
})

/**
 * @openapi
 * /mensas/{mensaId}:
 *   get:
 *     description: Get all Mensa meals of a certain mensa of today
 *     parameters:
 *       - in: path
 *         name: mensaId
 *         schema:
 *           type: integer
 *         required: true
 *         description: Numeric ID of the mensa to get the meals for
 *       - in: query
 *         name: date
 *         schema:
 *           type: string
 *           format: date
 *         description: Date for which to get the meals for (Today if not provided)
 *     responses:
 *       200:
 *         description: Lists all meals of a certain day
 */
app.get('/mensas/:id', (req, res) => {
  console.log(req.params.id);
  let date = req.query.date;
  if (!date) {
    const today = new Date();
    date = today.getFullYear() + '-' + (today.getMonth()+1+'').padStart(2, '0') + '-' + (today.getDay()+1+'').padStart(2, '0');
  }
  axios.get('https://api.studentenwerk-dresden.de/openmensa/v2/canteens/6/days/'+date+'/meals')
  .then(async function (response) {
    res.send({
      response: response.data,
    })
  })
})

const port = 3000;
app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})