package mocmien.com.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

import jakarta.validation.Valid;
import mocmien.com.dto.request.commission.AdminCommissionCreateRequest;
import mocmien.com.entity.AppCommission;
import mocmien.com.service.AdminCommissionService;

@RestController
@RequestMapping("/api/admin/commission")
@Validated
public class AdminCommissionController {

    @Autowired
    private AdminCommissionService commissionService;

    @PostMapping
    public ResponseEntity<AppCommission> create(@Valid @RequestBody AdminCommissionCreateRequest request) {
        AppCommission saved = commissionService.create(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<Page<AppCommission>> list(
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        return ResponseEntity.ok(commissionService.getPage(pageable));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<AppCommission> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(commissionService.setActive(id, true));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AppCommission> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(commissionService.setActive(id, false));
    }
}



