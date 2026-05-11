package org.viators.argo.goodsreceipt;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
