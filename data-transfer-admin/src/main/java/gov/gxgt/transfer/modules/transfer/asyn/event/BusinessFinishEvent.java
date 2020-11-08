package gov.gxgt.transfer.modules.transfer.asyn.event;

import org.springframework.context.ApplicationEvent;

public class BusinessFinishEvent extends ApplicationEvent {


    public BusinessFinishEvent(Object source) {
        super(source);
    }

}
