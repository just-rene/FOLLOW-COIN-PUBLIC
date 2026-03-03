package com.follow_coin.follow_coin_collect.tools;


import com.follow_coin.follow_coin_collect.types.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
//@Autowired
public class ClientInformationBuilder{


    @Getter
    public class ClientInformation {

        private ErrorCode internalErrorCode;
        private String info;
        private Object responseEntity;

        private ClientInformation(ClientInformationBuilder clientInformationBuilder) {
            this.internalErrorCode = clientInformationBuilder.internalErrorCode;
            this.info = clientInformationBuilder.info;
            this.responseEntity = clientInformationBuilder.responseEntity;
        }

    }

    private  ErrorCode internalErrorCode;
    private String info;
    private Object responseEntity;

    public ClientInformationBuilder setInternalErrorCode(ErrorCode internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
        return this;
    }

    public ClientInformationBuilder setInfo(String info){
        this.info = info;
        return this;
    }
    
    public ClientInformationBuilder setResponseEntity(Object responseEntity){
        this.responseEntity = responseEntity;
        return this;
    }

    public ClientInformation build(){
        return new ClientInformation(this);

    }

}

