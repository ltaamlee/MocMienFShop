package mocmien.com.dto.response;

import java.time.LocalDateTime;
import java.util.Map;


public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> messages;

    // private constructor, chỉ có builder mới gọi
    private ErrorResponse(Builder builder) {
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.error = builder.error;
        this.messages = builder.messages;
    }

    // Builder class
    public static class Builder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private Map<String, String> messages;

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder messages(Map<String, String> messages) {
            this.messages = messages;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }

    // Tạo static method tiện lợi
    public static Builder builder() {
        return new Builder();
    }

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}
   
}
