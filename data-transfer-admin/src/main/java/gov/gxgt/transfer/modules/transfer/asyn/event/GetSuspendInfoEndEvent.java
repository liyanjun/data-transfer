package gov.gxgt.transfer.modules.transfer.asyn.event;

import org.springframework.context.ApplicationEvent;

public class GetSuspendInfoEndEvent extends ApplicationEvent {


    public GetSuspendInfoEndEvent(Object source) {
        super(source);
    }

}
