package br.com.docket.cartorios.controller.web;

import br.com.docket.cartorios.client.CartorioApiClient;
import br.com.docket.cartorios.client.CertidaoApiClient;
import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.dto.CertidaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cartorios")
public class CartorioWebController {

    private final CartorioApiClient cartorioApiClient;
    private final CertidaoApiClient certidaoApiClient;

    @GetMapping
    public String findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String nome,
            Model model) {
        model.addAttribute("cartorios", cartorioApiClient.findAll(nome, page));
        model.addAttribute("nome", nome);
        return "cartorio/lista";
    }

    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("cartorioRequest", new CartorioRequest(null, null, null, null, null, null, null, null, Set.of()));
        model.addAttribute("certidoesDisponiveis", certidaoApiClient.listAll().content());
        return "cartorio/formulario";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute CartorioRequest cartorioRequest, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("certidoesDisponiveis", certidaoApiClient.listAll().content());
            return "cartorio/formulario";
        }
        cartorioApiClient.create(cartorioRequest);
        redirectAttributes.addFlashAttribute("mensagem", "Cartório cadastrado com sucesso.");
        return "redirect:/cartorios";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        CartorioResponse cartorio = cartorioApiClient.findById(id);

        Set<Long> certidaoIds = cartorio.certidoes().stream()
                .map(CertidaoResponse::id)
                .collect(Collectors.toSet());

        CartorioRequest cartorioRequest = new CartorioRequest(
                cartorio.nome(), cartorio.cep(), cartorio.rua(), cartorio.numero(),
                cartorio.complemento(), cartorio.bairro(), cartorio.cidade(), cartorio.uf(), certidaoIds);

        model.addAttribute("id", id);
        model.addAttribute("cartorioRequest", cartorioRequest);
        model.addAttribute("certidoesDisponiveis", certidaoApiClient.listAll().content());
        return "cartorio/formulario";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute CartorioRequest cartorioRequest, BindingResult result,
                             Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            model.addAttribute("certidoesDisponiveis", certidaoApiClient.listAll().content());
            return "cartorio/formulario";
        }

        cartorioApiClient.update(id, cartorioRequest);
        redirectAttributes.addFlashAttribute("mensagem", "Cartório atualizado com sucesso.");
        return "redirect:/cartorios";
    }

    @PostMapping("/{id}/excluir")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cartorioApiClient.delete(id);
        redirectAttributes.addFlashAttribute("mensagem", "Cartório excluído com sucesso.");
        return "redirect:/cartorios";
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public String handleNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", "Cartório não encontrado.");
        return "redirect:/cartorios";
    }
}
