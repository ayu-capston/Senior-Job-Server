package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.dto.LectureDto;
import com.seniorjob.seniorjobserver.service.LectureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {
	private final LectureService lectureService;
	public LectureController(LectureService lectureService) {
		this.lectureService = lectureService;
	}

	// GET /api/lectures
	@GetMapping
	public ResponseEntity<List<LectureDto>> getAllLectures() {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		for (LectureDto lectureDto : lectureList) {
			LectureEntity.LectureStatus status = lectureService.getLectureStatus(lectureDto.getCreate_id());
			lectureDto.setStatus(status);
		}
		return ResponseEntity.ok(lectureList);
	}

	// POST /api/lectures
	@PostMapping
	public ResponseEntity<LectureDto> createLecture(@RequestBody LectureDto lectureDto) {
		LectureDto createdLecture = lectureService.createLecture(lectureDto);
		return ResponseEntity.ok(createdLecture);
	}

	// PUT /api/lectures/{id}
	@PutMapping("/{id}")
	public ResponseEntity<LectureDto> updateLecture(@PathVariable("id") Long id, @RequestBody LectureDto lectureDto) {
		LectureDto updatedLecture = lectureService.updateLecture(id, lectureDto);
		return ResponseEntity.ok(updatedLecture);
	}

	// Delete /api/lectures/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLecture(@PathVariable("id") Long id) {
		lectureService.deleteLecture(id);
		return ResponseEntity.noContent().build();
	}

	// GET /api/lectures/{id}
	@GetMapping("/{id}")
	public ResponseEntity<LectureDto> getDetailLectureById(@PathVariable("id") Long id) {
		LectureDto lecture = lectureService.getDetailLectureById(id);
		if (lecture != null) {
			LectureEntity.LectureStatus status = lectureService.getLectureStatus(id);
			lecture.setStatus(status);
			return ResponseEntity.ok(lecture);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 최신순/ 오래된순
	// GET /api/lectures/sort/createdDate?descending=true
	// GET /api/lectures/sort/createdDate?descending=false
	@GetMapping("/sort/createdDate")
	public ResponseEntity<List<LectureDto>> sortLecturesByCreatedDate(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByCreatedDate(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 가격낮은순/가격높은순
	// GET /api/lectures/sort/price?descending=true
	// GET /api/lectures/sort/price?descending=false
	@GetMapping("/sort/price")
	public ResponseEntity<List<LectureDto>> sortLecturesByPrice(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByPrice(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 인기순
	// GET /api/lectures/sort/popularity?descending=true
	// GET /api/lectures/sort/popularity?descending=false
	@GetMapping("/sort/popularity")
	public ResponseEntity<List<LectureDto>> sortLecturesByPopularity(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByPopularity(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 강좌필터링API
	// GET /api/lectures/filtering?region={region}&status={status}&category={category}
	@GetMapping("/filtering")
	public ResponseEntity<List<LectureDto>> filterLectures(
			@RequestParam(required = false) String region,
			@RequestParam(required = false) LectureEntity.LectureStatus status,
			@RequestParam(required = false) String category) {

		List<LectureDto> filteredLectures = lectureService.filterLectures(region, status, category);

		for (LectureDto lectureDto : filteredLectures) {
			LectureEntity.LectureStatus lectureStatus = lectureService.getLectureStatus(lectureDto.getCreate_id());
			lectureDto.setStatus(lectureStatus);
		}

		return ResponseEntity.ok(filteredLectures);
	}




	// 페이징
	// GET /api/lectures/paging?page={no}&size={no}
	@GetMapping("/paging")
	public ResponseEntity<Page<LectureDto>> getLecturesWithPagination(
			@RequestParam(defaultValue = "0", name = "page") int page,
			@RequestParam(defaultValue = "12", name = "size") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<LectureEntity> lecturePage = lectureService.getLectures(pageable);

		List<LectureDto> lectureDtoList = lecturePage.getContent().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());

		Page<LectureDto> pagedLectureDto = new PageImpl<>(lectureDtoList, pageable, lecturePage.getTotalElements());

		return ResponseEntity.ok(pagedLectureDto);
	}


	private LectureDto convertToDto(LectureEntity lectureEntity) {
		if (lectureEntity == null)
			return null;

		return LectureDto.builder()
				.create_id(lectureEntity.getCreate_id())
				.creator(lectureEntity.getCreator())
				.max_participants(lectureEntity.getMaxParticipants())
				.category(lectureEntity.getCategory())
				.bank_name(lectureEntity.getBank_name())
				.account_name(lectureEntity.getAccount_name())
				.account_number(lectureEntity.getAccount_number())
				.price(lectureEntity.getPrice())
				.title(lectureEntity.getTitle())
				.content(lectureEntity.getContent())
				.cycle(lectureEntity.getCycle())
				.count(lectureEntity.getCount())
				.start_date(lectureEntity.getStart_date())
				.end_date(lectureEntity.getEnd_date())
				.region(lectureEntity.getRegion())
				.image_url(lectureEntity.getImage_url())
				.createdDate(lectureEntity.getCreatedDate())
				.build();
	}
}
