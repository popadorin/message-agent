# Message broker

The broker service is able to treat the messages from some senders which know about him and put the messages in some queues. 

The clients which have subscribed to some specific channel (queue) will be notified with the incomed message, by the broker.

## Commands 

#### Sender

* SEND (TYPE, CONTENT - of the message)
* EXIT

#### Receiver

* SUBSCRIBE (CHANNEL)
* SEND (TYPE, CONTENT - of the message)
* EXIT

#### Broker

* Receive messages from both Sender and Receiver
* Put the messages in corresponding queues


It was used **TCP** as transport level protocol of message-communication between entitites, because it is connection-based and there is no need to make additional stepts in order to guarantee that the messages content and packets order in the transmission process will be safe.

