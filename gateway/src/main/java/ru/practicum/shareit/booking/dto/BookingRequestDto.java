package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
	@NotNull(message = "Start of booking must not be null")
	@Future(message = "Start of booking must be in future")
	private LocalDateTime start;
	@NotNull(message = "End of booking must not be null")
	@Future(message = "End of booking must be in future")
	private LocalDateTime end;
	@NotNull(message = "Booking item ID must not be null")
	private Long itemId;
	private Long bookerId;
}