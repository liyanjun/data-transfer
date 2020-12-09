package gov.gxgt.transfer.modules.transfer.asyn.event;

import org.springframework.context.ApplicationEvent;

public class GetSuspendInfoEvent extends ApplicationEvent {


    public GetSuspendInfoEvent(Object source) {
        super(source);
    }

}
