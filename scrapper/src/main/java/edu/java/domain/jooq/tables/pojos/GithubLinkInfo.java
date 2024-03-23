/*
 * This file is generated by jOOQ.
 */
package edu.java.domain.jooq.tables.pojos;


import java.beans.ConstructorProperties;
import java.io.Serializable;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class GithubLinkInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long linkId;
    private Long lastEventId;

    public GithubLinkInfo() {}

    public GithubLinkInfo(GithubLinkInfo value) {
        this.id = value.id;
        this.linkId = value.linkId;
        this.lastEventId = value.lastEventId;
    }

    @ConstructorProperties({ "id", "linkId", "lastEventId" })
    public GithubLinkInfo(
        @Nullable Long id,
        @NotNull Long linkId,
        @NotNull Long lastEventId
    ) {
        this.id = id;
        this.linkId = linkId;
        this.lastEventId = lastEventId;
    }

    @Nullable
    public Long getId() {
        return this.id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getLinkId() {
        return this.linkId;
    }

    public void setLinkId(@NotNull Long linkId) {
        this.linkId = linkId;
    }

    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getLastEventId() {
        return this.lastEventId;
    }

    public void setLastEventId(@NotNull Long lastEventId) {
        this.lastEventId = lastEventId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GithubLinkInfo other = (GithubLinkInfo) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.linkId == null) {
            if (other.linkId != null)
                return false;
        }
        else if (!this.linkId.equals(other.linkId))
            return false;
        if (this.lastEventId == null) {
            if (other.lastEventId != null)
                return false;
        }
        else if (!this.lastEventId.equals(other.lastEventId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.linkId == null) ? 0 : this.linkId.hashCode());
        result = prime * result + ((this.lastEventId == null) ? 0 : this.lastEventId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GithubLinkInfo (");

        sb.append(id);
        sb.append(", ").append(linkId);
        sb.append(", ").append(lastEventId);

        sb.append(")");
        return sb.toString();
    }
}
