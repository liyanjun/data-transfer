package gov.gxgt.transfer.modules.transfer.asyn.event;

import org.springframework.context.ApplicationEvent;

public class BusinessAcceptEvent extends ApplicationEvent {


    public BusinessAcceptEvent(Object source) {
        super(source);
    }

}
