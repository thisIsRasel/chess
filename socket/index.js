var app = require("express")();

var http = require("http").Server(app);

var io = require("socket.io")(http);

var players = [];
var invitations = {};

app.get("/", function(request, response){
	response.sendFile(__dirname + '/index.html');
});

io.on("connection", function(socket){

	console.log("user connnected = " + socket.id);

	socket.on("setUser", function(data) {

		var info = JSON.parse(data);

		var player = new Object();
		player.socketId = socket.id;
		player.androidId = info.androidId;
		player.username = info.username;
		players.push(player);
	});

	socket.on("players", function(){
		io.emit("players", { players: players });
	});

	socket.on("invitations", function() {
		io.emit("invitations", { invitations: invitations });
	});

	socket.on("invite", function(deviceId){

		var sender = null;

		for(var i=0; i<players.length; i++) {

			if(players[i].socketId == socket.id) {

				sender = players[i];
				break;
			}
		}

		var flag = true;

		if(deviceId in invitations) {

			for(var i=0; i<invitations[deviceId].length; i++) {

				if(invitations[deviceId][i].socketId == sender.socketId) {

					flag = false;
					break;
				}
			}

		} else {

			invitations[deviceId] = [];
		}

		if(flag) {

			invitations[deviceId].push(sender);
		}
	
		io.emit("invitations", { invitations: invitations });
	});

	socket.on("confirmation", function(data) {
		var flag = false;
		var pair = JSON.parse(data);

		for(var i=0; i<invitations[pair.from].length; i++){
			if(invitations[pair.from][i].androidId == pair.to) {
				flag = true;
				invitations[pair.from].splice(i, 1);
				break;
			}
		}

		if(flag) { io.emit("confirmation", pair); }
	});

	socket.on("cancelInvitation", function(data) {

		var pair = JSON.parse(data);

		if(pair.to in invitations) {
			for(var i=0; i<invitations[pair.to].length; i++) {
				if(invitations[pair.to][i].androidId == pair.from) {
					invitations[pair.to].splice(i, 1);
					break;
				}
			}

			io.emit("invitations", { invitations: invitations });
		}
	});

	socket.on("message", function(msg){
		console.log(msg);
		io.emit("message", msg);
	});

	socket.on("data", function(data) {
		var chessData = JSON.parse(data);
		io.emit("data", chessData);
	});

	socket.on("disconnect", function() {

		console.log("user disconnected = " + socket.id);

		// Remove players as player is offline
		var deviceId = "";
		for(var i=0; i<players.length; i++) {
			if(players[i].socketId == socket.id) {
				deviceId = players[i].androidId;
				players.splice(i, 1);
				break;
			}
		}

		// Remove invitations as player is offline
		if(deviceId in invitations) { delete invitations[deviceId]; } // player's invitation list

		// Invitation list where player is in
		for(x in invitations) {
			for(var i=0; i<invitations[x].length; i++) {
				if(invitations[x][i].androidId == deviceId) {
					invitations[x].splice(i, 1);
					break;
				}
			}
		}

		io.emit("players", { players: players });
		io.emit("invitations", { invitations: invitations });
	});
});

http.listen(3000, function() {
	console.log("server listening on port 3000");
});
