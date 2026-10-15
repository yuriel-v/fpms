/**
 * Inbound web DTOs (request/response/error shapes) for the auth slice.
 *
 * <p>These types live at the web edge and never leak into the application or domain layers; mapping
 * to commands and from results/domain objects happens in the controllers or these DTOs' own factory
 * methods.
 */
package dev.yuriel.fpms.adapter.in.web.dto;
