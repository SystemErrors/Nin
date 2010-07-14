/**
 * @name : welcome_Msg.js
 * @location : Map 0
 * @author : Traitor
 * @function : Sends the welcome msg
 */
var messages = Array("Welcome to NinjaMS", "Hello and Welcome to NinjaMS", "Ohaithar rookie ninja. Welcome to NinjaMS");

function start(ms) {
    if (ms.getPlayer().getMap().getId() == 0) {
        ms.getPlayer().startMapEffect(messages[(Math.random() * messages.length) | 0], 5120024);
    } 
}