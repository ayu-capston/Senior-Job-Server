package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.dto.LectureDto;
import com.seniorjob.seniorjobserver.service.LectureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	// GET /api/lectures/sort/latest?descending=true
	// GET /api/lectures/sort/latest?descending=false
	@GetMapping("/sort/createdDate")
	public ResponseEntity<List<LectureDto>> sortLecturesByCreatedDate(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByCreatedDate(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// GET /api/lectures/sort/price?descending=true
	// GET /api/lectures/sort/price?descending=false
	@GetMapping("/sort/price")
	public ResponseEntity<List<LectureDto>> sortLecturesByPrice(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByPrice(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// GET /api/lectures/sort/popularity?descending=true
	// GET /api/lectures/sort/popularity?descending=false
	@GetMapping("/sort/popularity")
	public ResponseEntity<List<LectureDto>> sortLecturesByPopularity(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByPopularity(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 강좌필터링검색API
	// 강좌제목
	// GET http://localhost:8080/api/lectures/search={title}
	// 강좌제목 + 상태(모집중, 개설대기중, 마감)
	// GET /api/lectures/search?title={title}&status={status}
	// 강좌제목 + 상태(모집중, 개설대기중, 마감) + 최신순(false)/오래된순(true)
	// GET api/lectures/search?title={title}&status={WAITING/RECRUITING/CLOSED}&descending={true/false}
	// 강좌제목 + 상태(모집중, 개설대기중, 마감) + 최신순(false)/오래된순(true) + 인기순(false)/참여자적은순(true)
	// GET /api/lectures/search?title={강좌제목}&status={상태}&descending={최신순/오래된순}&sortByPopularity={인기순/참여자적은순}
	// 강좌제목 + 상태(모집중, 개설대기중, 마감) + 최신순(false)/오래된순(true) + 인기순(false)/참여자적은순(true) + 가격높은순(false)/낮은순(true)
	// GET /api/lectures/search?title={강좌제목}&status={상태}&descending={최신순/오래된순}&sortByPopularity={인기순/참여자적은순}&sortLecturesByPrice{가격순}
//	@GetMapping("/search")
//	public ResponseEntity<List<LectureDto>> searchLectures(
//			@RequestParam(value = "title", required = false) String title,
//			@RequestParam(value = "status", required = false) LectureEntity.LectureStatus status,
//			@RequestParam(value = "sortByPopularity", defaultValue = "false") boolean sortByPopularity,
//			@RequestParam(value = "descending", defaultValue = "false") boolean descending,
//			@RequestParam(value = "sortByPrice", defaultValue = "false") boolean sortByPrice,
//			@RequestParam(value = "priceDescending", defaultValue = "false") boolean priceDescending) {
//
//		List<LectureDto> lectureList;
//		if (title != null && status != null) {
//			lectureList = lectureService.searchLecturesByTitleAndStatus(title, status);
//		} else if (title != null) {
//			lectureList = lectureService.searchLecturesByTitle(title);
//		} else if (status != null) {
//			lectureList = lectureService.searchLecturesByStatus(status);
//		} else {
//			return ResponseEntity.badRequest().build();
//		}
//
//		for (LectureDto lectureDto : lectureList) {
//			LectureEntity.LectureStatus lectureStatus = lectureService.getLectureStatus(lectureDto.getCreate_id());
//			lectureDto.setStatus(lectureStatus);
//		}
//
//		if (sortByPopularity) {
//			lectureList = lectureService.sortLecturesByPopularity(lectureList, descending);
//		} else if (sortByPrice) {
//			lectureList = lectureService.sortLecturesByPrice(lectureList, priceDescending);
//		} else {
//			lectureList = lectureService.sortLecturesByCreatedDate(lectureList, descending);
//		}
//
//		return ResponseEntity.ok(lectureList);
//	}

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
