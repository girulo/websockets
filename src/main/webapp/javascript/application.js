angular.module('angular.atmosphere.chat', ['angular.atmosphere']);

function ChatController($scope, atmosphereService){
  $scope.model = {
    transport: 'websocket',
    messages: []
  };

  var socket;

  var request = {
    url: '/challenge',
    contentType: 'application/json',
    logLevel: 'debug',
    transport: 'websocket',
    trackMessageLength: true,
    reconnectInterval: 5000,
    enableXDR: true,
    timeout: 60000,

  };

  request.onOpen = function(response){
    $scope.model.transport = response.transport;
    $scope.model.connected = true;
    $scope.model.content = 'Atmosphere connected using ' + response.transport;
  };

  request.onClientTimeout = function(response){
    $scope.model.content = 'Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval;
    $scope.model.connected = false;
    socket.push(atmosphere.util.stringifyJSON({ senderId: senderId, pushMessage: 'is inactive and closed the connection. Will reconnect in ' + request.reconnectInterval }));
    setTimeout(function(){
      socket = atmosphereService.subscribe(request);
    }, request.reconnectInterval);
  };

  request.onReopen = function(response){
    $scope.model.connected = true;
    $scope.model.content = 'Atmosphere re-connected using ' + response.transport;
  };

  //For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
  request.onTransportFailure = function(errorMsg, request){
    atmosphere.util.info(errorMsg);
    request.fallbackTransport = 'long-polling';
    $scope.model.header = 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport;
  };

  request.onMessage = function(response){
    var responseText = response.responseBody;
    try{
      var message = atmosphere.util.parseJSON(responseText);
      if(!$scope.model.logged && $scope.model.senderId)
        $scope.model.logged = true;
      else{
        $scope.model.messages.push({pushMessage: message.pushMessage, senderId: message.senderId});
      }
    }catch(e){
      console.error("Error parsing JSON: ", responseText);
      throw e;
    }
  };

  request.onClose = function(response){
    $scope.model.connected = false;
    $scope.model.content = 'Server closed the connection after a timeout';
    socket.push(atmosphere.util.stringifyJSON({ senderId: $scope.model.senderId, pushMessage: 'disconnecting' }));
  };

  request.onError = function(response){
    $scope.model.content = "Sorry, but there's some problem with your socket or the server is down";
    $scope.model.logged = false;
  };

  request.onReconnect = function(request, response){
    $scope.model.content = 'Connection lost. Trying to reconnect ' + request.reconnectInterval;
    $scope.model.connected = false;
  };

  socket = atmosphereService.subscribe(request);

}
