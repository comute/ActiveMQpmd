

// AMQ  handler
var amq = 
{
  poll: true,
  _first: true,
  _pollEvent: function(first) {},
  _handlers: new Array(),
  
  _messages:0,
  _messageQueue: '',
  _queueMessages: false,
  
  _messageHandler: function(request) 
  {
    try
    {
      if (request.status == 200)
      {
        var response = request.responseXML.getElementsByTagName("ajax-response");
        if (response != null && response.length == 1)
        {
          for ( var i = 0 ; i < response[0].childNodes.length ; i++ ) 
          {
            var responseElement = response[0].childNodes[i];
	    
            // only process nodes of type element.....
            if ( responseElement.nodeType != 1 )
              continue;

            var id   = responseElement.getAttribute('id');
            
            
            var handler = amq._handlers[id];
            if (handler!=null)
            {
              for (var j = 0; j < responseElement.childNodes.length; j++) 
              {
                var child = responseElement.childNodes[j]
                if (child.nodeType == 1) 
                {
                  handler(child);
                }
        	      }
            }
          }
        }
      }
    }
    catch(e)
    {
      alert(e);
    }
  },
  
  _pollHandler: function(request) 
  {
    amq._queueMessages=true;
    try
    {
      amq._messageHandler(request);
      amq._pollEvent(amq._first);
      amq._first=false;
    }
    catch(e)
    {
        alert(e);
    }
    
    amq._queueMessages=false;
    
    if (amq._messages==0)
    {
      if (amq.poll)
        new Ajax.Request('/amq', { method: 'get', onSuccess: amq._pollHandler }); 
    }
    else
    {
      var body = amq._messageQueue+'&poll='+amq.poll;
      amq._messageQueue='';
      amq._messages=0;
      new Ajax.Request('/amq', { method: 'post', onSuccess: amq._pollHandler, postBody: body }); 
    }
  },
  
  addPollHandler : function(func)
  {
    var old = amq._pollEvent;
    amq._pollEvent = function(first) 
    {
      old(first);
      func(first);
    }
  },
  
  sendMessage : function(destination,message)
  {
   amq._sendMessage(destination,message,'send');
  },
  
  // Listen on a channel or topic.   handler must be a function taking a message arguement
  addListener : function(id,destination,handler)
  {   
    amq._handlers[id]=handler;
    amq._sendMessage(destination,id,'listen');
  },
  
  // remove Listener from channel or topic.  
  removeListener : function(destination)
  {   
    amq._sendMessage(destination,'','unlisten');
  },
  
  _sendMessage : function(destination,message,type)
  {
    if (amq._queueMessages)
    {
      amq._messageQueue+=(amq._messages==0?'destination=':'&destination=')+destination+'&message='+message+'&type='+type;
      amq._messages++;
    }
    else
    {
      new Ajax.Request('/amq', { method: 'post', postBody: 'destination='+destination+'&message='+message+'&type='+type});
    }
  },
  
  _startPolling : function()
  {
    if (amq.poll)
      new Ajax.Request('/amq', { method: 'get', parameters: 'timeout=0', onSuccess: amq._pollHandler });
  }
};

Behaviour.addLoadEvent(amq._startPolling);  

