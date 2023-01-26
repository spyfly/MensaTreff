/* istanbul ignore file */
const axios = require('axios');

// Sets up the routes.
module.exports.setup = (app) => {
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
                    const openingDays = await axios.get('https://api.studentenwerk-dresden.de/openmensa/v2/canteens/' + mensa.id + '/days');
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
            date = today.getFullYear() + '-' + (today.getMonth() + 1 + '').padStart(2, '0') + '-' + (today.getDay() + 1 + '').padStart(2, '0');
            console.log(date)
        }
        axios.get('https://api.studentenwerk-dresden.de/openmensa/v2/canteens/' + req.params.id + '/days/' + date + '/meals')
            .then(async function (response) {
                res.send({
                    response: response.data,
                })
            })
    })
}