/* istanbul ignore file */
const randomstring = require("randomstring");
const getUser = require('../functions/getUser.js');

/**
 * @openapi
 * components:
 *   securitySchemes:
 *     bearerAuth:
 *       type: http
 *       scheme: bearer
 */

// Sets up the routes.
module.exports.setup = (app, db) => {
    /**
     * @openapi
     * /user:
     *   get:
     *     description: Read user account details
     *     security:
     *       - bearerAuth: []
     *     responses:
     *       200:
     *         description: User Account details
     *       403:
     *         description: No auth header sent
     *       404:
     *         description: No account found
     */

    app.get("/user", async (req, res) => {
        const user = await getUser(req, res, db);
        if (user) {
            res.send(user);
        }
    });

    /**
    * @openapi
    * /user:
    *   post:
    *     description: Create new User Account
    *     requestBody:
    *       content:
    *         application/x-www-form-urlencoded:
    *           schema:
    *             type: object
    *             properties:
    *               username:
    *                 type: string
    *     responses:
    *       200:
    *         description: User Account created successfully
    *       400:
    *         description: No username provided
    */
    app.post("/user", (req, res) => {
        if (req.body.username) {
            const username = req.body.username;
            const passkey = randomstring.generate(32)
            db.run('INSERT INTO users (username, passkey) VALUES ("' + username + '", "' + passkey + '")')
            res.send({
                username: username,
                passkey: passkey
            })
        } else {
            res.sendStatus(400)
        }
    })
}