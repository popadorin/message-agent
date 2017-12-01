# Message broker

The broker service is able to treat the messages from some senders which know about him and put the messages in some queues. 

The clients which have subscribed to some specific channel (queue) will be notified with the incomed message, by the broker.

## Commands 

#### Sender

* CREATE (TOPIC, QUEUE_TYPE)
* SEND (TYPE, CONTENT - of the message). `TYPE - PUT or GET`
* EXIT

#### Receiver

* CREATE (TOPIC, QUEUE_TYPE)
* SUBSCRIBE (CHANNEL)
* SEND (TYPE, CONTENT - of the message).  `TYPE - PUT or GET`
* EXIT


#### Broker

* Receive messages from both Sender and Receiver
* Put the messages in corresponding queues
* Forward message when queue updated to corresponding subscriber if any
* If queue is non-persistent and the messages are gone, it is deleted - otherwise it stays alive
* Create dynamicly specified queues from client

It was used **TCP** as transport level protocol of message-communication between entitites, because it is connection-based and there is no need to make additional stepts in order to guarantee that the messages content and packets order in the transmission process will be safe.

Conditions for laboratory - https://github.com/Alexx-G/PAD-labs/blob/master/PAD-lab1.md
