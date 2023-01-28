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
            const rows = await db.all('SELECT timeslot, username, userId FROM matches LEFT JOIN users ON matches.userId = users.id WHERE mensaId = ' + mensaId + ' AND date = "' + date + '"')
            var resp = {
                timeslots: {}
            };
            for (const row of rows) {
                if (!resp.timeslots[row.timeslot]) {
                    resp.timeslots[row.timeslot] = {
                        participantCount: 0,
                        participantNames: [],
                        participating: false
                    };
                }
                if (row.userId == user.id) {
                    resp.timeslots[row.timeslot].participating = true
                } else {
                    resp.timeslots[row.timeslot].participantNames.push(row.username)
                }
                resp.timeslots[row.timeslot].participantCount++;
            }
            res.send(resp)
        }
    })

    /**
     * @openapi
     * /match/{mensaId}/{date}/{timeslot}:
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
     *       - in: path
     *         name: timeslot
     *         schema:
     *           type: string
     *         description: Timeslot for which to get the matches for
     *     responses:
     *       200:
     *         description: Lists all possible matches for a certain day
     */
    app.get("/match/:mensaId/:date/:timeslot", async (req, res) => {
        const user = await getUser(req, res, db);
        if (user) {
            const mensaId = req.params.mensaId
            const date = req.params.date
            const timeslot = req.params.timeslot
            const rows = await db.all('SELECT timeslot, username, userId FROM matches LEFT JOIN users ON matches.userId = users.id WHERE mensaId = ' + mensaId + ' AND date = "' + date + '" AND timeslot = "' + timeslot + '"')
            var resp = {
                participantCount: 0,
                participantNames: [],
                participating: false
            };
            for (const row of rows) {
                if (row.userId == user.id) {
                    resp.participating = true
                } else {
                    resp.participantNames.push(row.username)
                }
                resp.participantCount++;
            }
            res.send(resp)
        }
    })

    /**
     * @openapi
     * /match/{mensaId}/{date}/{timeslot}:
     *   post:
     *     description: Select timeslot for a certain day and mensa
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
     *       - in: path
     *         name: timeslot
     *         schema:
     *           type: string
     *         description: Timeslot for which to get the matches for
     *     responses:
     *       200:
     *         description: Lists all possible matches for a certain day
     */
    app.post("/match/:mensaId/:date/:timeslot", async (req, res) => {
        const user = await getUser(req, res, db);
        if (user) {
            const mensaId = req.params.mensaId
            const date = req.params.date
            const timeslot = req.params.timeslot
            const duplicates = await db.all('SELECT * FROM matches WHERE mensaId = ' + mensaId + ' AND date = "' + date + '" AND timeslot = "' + timeslot + '" AND userId = ' + user.id)
            if (duplicates.length == 0) {
                await db.run('INSERT INTO matches (mensaId, date, timeslot, userId) VALUES (' + mensaId + ', "' + date + '", "' + timeslot + '", ' + user.id + ')')
                res.sendStatus(200)
            } else {
                res.sendStatus(403)
            }
        }
    })

    /**
 * @openapi
 * /match/{mensaId}/{date}/{timeslot}:
 *   delete:
 *     description: Delete timeslot for a certain day and mensa
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
 *       - in: path
 *         name: timeslot
 *         schema:
 *           type: string
 *         description: Timeslot for which to get the matches for
 *     responses:
 *       200:
 *         description: Lists all possible matches for a certain day
 */
    app.delete("/match/:mensaId/:date/:timeslot", async (req, res) => {
        const user = await getUser(req, res, db);
        if (user) {
            const mensaId = req.params.mensaId
            const date = req.params.date
            const timeslot = req.params.timeslot
            const duplicates = await db.all('SELECT * FROM matches WHERE mensaId = ' + mensaId + ' AND date = "' + date + '" AND timeslot = "' + timeslot + '" AND userId = ' + user.id)
            if (duplicates.length == 1) {
                await db.run('DELETE FROM matches WHERE mensaId = ' + mensaId + ' AND date = "' + date + '" AND timeslot = "' + timeslot + '" AND userId = ' + user.id)
                res.sendStatus(200)
            } else {
                res.sendStatus(404)
            }
        }
    })
}