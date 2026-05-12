package org.viators.argo.goodsreceipt;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.goodsreceipt.dto.request.CancelGoodsReceiptRequest;
import org.viators.argo.goodsreceipt.dto.request.CreateGoodsReceiptRequest;
import org.viators.argo.goodsreceipt.dto.response.GoodsReceiptDetailsResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/goods-receipts")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService goodsReceiptService;

    @PreAuthorize("hasAnyRole('PROCUREMENT_CLERK', 'PROCUREMENT_MANAGER')")
    @PostMapping
    public ResponseEntity<GoodsReceiptDetailsResponse> create(
        @Valid @RequestBody CreateGoodsReceiptRequest request
    ) {
        GoodsReceiptDetailsResponse response = goodsReceiptService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/goods-receipts/" + response.goodsReceiptPublicId()))
            .body(response);
    }

    @GetMapping("/{receiptPublicId}")
    public ResponseEntity<GoodsReceiptDetailsResponse> getGoodsReceipt(@PathVariable String receiptPublicId) {
        return ResponseEntity.ok(
            goodsReceiptService.getGoodsReceipt(receiptPublicId)
        );
    }

    @PatchMapping("/{receiptPublicId}")
    public ResponseEntity<Void> cancelGoodsReceipt(
        @CurrentKeycloakId String keycloakPublicId,
        @PathVariable String receiptPublicId,
        @Valid @RequestBody CancelGoodsReceiptRequest request
    ) {
        goodsReceiptService.cancelGoodsReceipt(keycloakPublicId, receiptPublicId, request);
        return ResponseEntity.noContent().build();
    }

}
