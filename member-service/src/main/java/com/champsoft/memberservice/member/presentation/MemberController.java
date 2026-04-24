package com.champsoft.memberservice.member.presentation;

import com.champsoft.memberservice.member.application.MemberService;
import com.champsoft.memberservice.member.domain.MemberResponse;
import com.champsoft.memberservice.member.domain.UpsertMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all members")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully")
    })
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "Retrieve a member by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member found"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public MemberResponse getMember(@PathVariable UUID memberId) {
        return memberService.getMemberById(memberId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Duplicate member email"),
            @ApiResponse(responseCode = "422", description = "Invalid member state")
    })
    public MemberResponse createMember(@Valid @RequestBody UpsertMemberRequest request) {
        return memberService.createMember(request);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "Update a member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Member not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate member email"),
            @ApiResponse(responseCode = "422", description = "Invalid member state")
    })
    public MemberResponse updateMember(@PathVariable UUID memberId, @Valid @RequestBody UpsertMemberRequest request) {
        return memberService.updateMember(memberId, request);
    }

    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a member")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member deleted"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public void deleteMember(@PathVariable UUID memberId) {
        memberService.deleteMember(memberId);
    }
}
