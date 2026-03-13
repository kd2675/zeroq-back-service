package com.zeroq.back.service.gateway.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminGatewayRepository;
import com.zeroq.back.service.gateway.vo.CreateGatewayRequest;
import com.zeroq.back.service.gateway.vo.GatewayRegistryResponse;
import com.zeroq.back.service.space.biz.SpaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTests {

    @Mock
    private AdminGatewayRepository adminGatewayRepository;

    @Mock
    private SpaceService spaceService;

    @InjectMocks
    private GatewayService gatewayService;

    @Test
    void createGateway_persistsOwnedGatewayRegistration() {
        CreateGatewayRequest request = new CreateGatewayRequest(
                "GW-ALPHA-01",
                "Gateway Alpha 01",
                44L,
                "EDGE",
                "NORTH",
                "North aisle",
                "192.168.0.10",
                64,
                "v1.0.0",
                "Primary chair cluster gateway",
                "ble://alpha-01"
        );
        AdminSpace ownedSpace = AdminSpace.builder()
                .id(44L)
                .ownerProfileId(8L)
                .spaceCode("SPACE-44")
                .name("Archive Vault")
                .build();

        when(adminGatewayRepository.existsByGatewayId("GW-ALPHA-01")).thenReturn(false);
        when(spaceService.getOwnedSpace(44L, 8L, false)).thenReturn(ownedSpace);
        when(adminGatewayRepository.save(any(AdminGateway.class))).thenAnswer(invocation -> {
            AdminGateway gateway = invocation.getArgument(0);
            gateway.setId(91L);
            return gateway;
        });

        GatewayRegistryResponse response = gatewayService.createGateway(request, 8L, false);

        ArgumentCaptor<AdminGateway> captor = ArgumentCaptor.forClass(AdminGateway.class);
        verify(adminGatewayRepository).save(captor.capture());
        AdminGateway saved = captor.getValue();

        assertThat(saved.getGatewayId()).isEqualTo("GW-ALPHA-01");
        assertThat(saved.getSpaceId()).isEqualTo(44L);
        assertThat(saved.getSensorCapacity()).isEqualTo(64);
        assertThat(saved.getStatus()).isEqualTo("OFFLINE");
        assertThat(response.getGatewayId()).isEqualTo("GW-ALPHA-01");
        assertThat(response.getSpaceName()).isEqualTo("Archive Vault");
        assertThat(response.getStatus()).isEqualTo("OFFLINE");
    }

    @Test
    void createGateway_rejectsDuplicateGatewayId() {
        CreateGatewayRequest request = new CreateGatewayRequest(
                "GW-DUP-01",
                "Duplicate Gateway",
                12L,
                null,
                null,
                null,
                null,
                24,
                null,
                null,
                null
        );
        when(adminGatewayRepository.existsByGatewayId("GW-DUP-01")).thenReturn(true);

        assertThatThrownBy(() -> gatewayService.createGateway(request, 5L, false))
                .isInstanceOf(LiveSpaceException.ConflictException.class)
                .hasMessageContaining("이미 등록된 gatewayId");
    }
}
