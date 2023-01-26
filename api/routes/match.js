/* istanbul ignore file */
const getUser = require('../functions/getUser.js');

// Sets up the routes.
module.exports.setup = (app, db) => {
    /**
     * @openapi
     * /match/{mensaId}/{date}:
     *   get:
     *     description: Get possible matches for a certain day and
     *     security:
     *       - bearerAuth: []
     *     parameters:
     *       - in: path
     *         name: mensaId
     *         schema:
     *           type: integer
     *         required: true
     *         description: Numeric ID of the mensa to get matches for
     *       - in: path
     *         name: date
     *         schema:
     *           type: string
     *           format: date
     *         description: Date for which to get the matches for
     *     responses:
     *       200:
     *         description: Lists all possible matches for a certain day
     */
    app.get("/match/:mensaId/:date", async (req, res) => {
        const user = await getUser(req, res, db);
        if (user) {
            const mensaId = req.params.mensaId
            const date = req.params.date
            const rows = await db.all('SELECT * FROM matches WHERE mensaId = '+mensaId+' AND date = "'+date+'"')
            res.send(rows)
        }
    })
}