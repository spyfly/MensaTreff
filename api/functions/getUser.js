module.exports = async (req, res, db) => {
    if (req.headers.authorization && req.headers.authorization.startsWith('Bearer ')) {
        const passkey = req.headers.authorization.replace('Bearer ', '');
        const result = await db.all('SELECT * FROM users WHERE passkey = "' + passkey + '"');
        if (result.length == 0) {
            res.sendStatus(404)
        } else {
            return result[0];
        }
    } else {
        res.sendStatus(403)
    }
    return null;
};