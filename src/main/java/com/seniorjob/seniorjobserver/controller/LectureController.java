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

	// 강좌개설API
	// POST /api/lectures
	@PostMapping
	public ResponseEntity<LectureDto> createLecture(@RequestBody LectureDto lectureDto) {
		LectureDto createdLecture = lectureService.createLecture(lectureDto);
		return ResponseEntity.ok(createdLecture);
	}

	// 개설된강좌수정API
	// PUT /api/lectures/{id}
	@PutMapping("/{id}")
	public ResponseEntity<LectureDto> updateLecture(@PathVariable("id") Long id, @RequestBody LectureDto lectureDto) {
		LectureDto updatedLecture = lectureService.updateLecture(id, lectureDto);
		return ResponseEntity.ok(updatedLecture);
	}

	// 강좌전체조회API
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

	// 개설된강좌삭제API
	// Delete /api/lectures/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLecture(@PathVariable("id") Long id) {
		lectureService.deleteLecture(id);
		return ResponseEntity.noContent().build();
	}

	// 개설된강좌상세정보API
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

	// 강좌최신순/오래된순 정렬
	// GET /api/lectures/sort/latest?descending=true
	// GET /api/lectures/sort/latest?descending=false
	@GetMapping("/sort/createdDate")
	public ResponseEntity<List<LectureDto>> sortLecturesByCreatedDate(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByCreatedDate(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 강좌가격순 정렬
	// GET /api/lectures/sort/price?descending=true
	// GET /api/lectures/sort/price?descending=false
	@GetMapping("/sort/price")
	public ResponseEntity<List<LectureDto>> sortLecturesByPrice(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByPrice(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 강좌 인기순 정렬
	// GET /api/lectures/sort/popularity?descending=true
	// GET /api/lectures/sort/popularity?descending=false
	@GetMapping("/sort/popularity")
	public ResponseEntity<List<LectureDto>> sortLecturesByPopularity(@RequestParam(value = "descending", defaultValue = "false") boolean descending) {
		List<LectureDto> lectureList = lectureService.getAllLectures();
		lectureList = lectureService.sortLecturesByPopularity(lectureList, descending);
		return ResponseEntity.ok(lectureList);
	}

	// 강좌제목검색API
	// Get /api/lectures/search?title={문자}
	@GetMapping("/search")
	public ResponseEntity<List<LectureDto>> searchLecturesByTitle(@RequestParam("title") String title){
		List<LectureDto> lectureList = lectureService.searchLecturesByTitle(title);
		for(LectureDto lectureDto : lectureList){
			LectureEntity.LectureStatus status = lectureService.getLectureStatus(lectureDto.getCreate_id());
			lectureDto.setStatus(status);
		}
		return ResponseEntity.ok(lectureList);
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
