package cn.tuyucheng.taketoday.akkahttp;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.FI;


class UserActor extends AbstractActor {

    private UserService userService = new UserService();

    static Props props() {
        return Props.create(UserActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
              .match(UserMessages.CreateUserMessage.class, handleCreateUser())
              .match(UserMessages.GetUserMessage.class, handleGetUser())
              .build();
    }

    private FI.UnitApply<UserMessages.CreateUserMessage> handleCreateUser() {
        return createUserMessageMessage -> {
            userService.createUser(createUserMessageMessage.getUser());
            sender().tell(new UserMessages.ActionPerformed(String.format("User %s created.", createUserMessageMessage.getUser()
                  .getName())), getSelf());
        };
    }

    private FI.UnitApply<UserMessages.GetUserMessage> handleGetUser() {
        return getUserMessageMessage -> {
            sender().tell(userService.getUser(getUserMessageMessage.getUserId()), getSelf());
        };
    }

}
