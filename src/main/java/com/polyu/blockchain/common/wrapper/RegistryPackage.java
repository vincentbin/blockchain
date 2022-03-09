package com.polyu.blockchain.common.wrapper;

import com.polyu.blockchain.common.util.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class RegistryPackage {
    private String host;
    private int port;

    public RegistryPackage(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String toJson() {
        return JsonUtil.objectToJson(this);
    }

    public static RegistryPackage fromJson(String json) {
        return JsonUtil.jsonToObject(json, RegistryPackage.class);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistryPackage that = (RegistryPackage) o;
        return port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

}
