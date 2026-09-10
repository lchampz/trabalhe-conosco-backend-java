package br.com.docket.cartorios.controller.web;

import br.com.docket.cartorios.client.CertidaoApiClient;
import br.com.docket.cartorios.dto.CertidaoRequest;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/certidoes")
public class CertidaoWebController {

    private final CertidaoApiClient certidaoApiClient;

    @GetMapping
    public String findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String nome,
            Model model) {
        model.addAttribute("certidoes", certidaoApiClient.findAll(nome, page));
        model.addAttribute("nome", nome);
        return "certidao/lista";
    }

    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("certidaoRequest", new CertidaoRequest(null));
        return "certidao/formulario";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute CertidaoRequest certidaoRequest, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "certidao/formulario";
        }
        certidaoApiClient.create(certidaoRequest);
        redirectAttributes.addFlashAttribute("mensagem", "Certidão cadastrada com sucesso.");
        return "redirect:/certidoes";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        var certidao = certidaoApiClient.findById(id);
        model.addAttribute("id", id);
        model.addAttribute("certidaoRequest", new CertidaoRequest(certidao.nome()));
        return "certidao/formulario";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute CertidaoRequest certidaoRequest, BindingResult result,
                             Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "certidao/formulario";
        }
        certidaoApiClient.update(id, certidaoRequest);
        redirectAttributes.addFlashAttribute("mensagem", "Certidão atualizada com sucesso.");
        return "redirect:/certidoes";
    }

    @PostMapping("/{id}/excluir")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        certidaoApiClient.delete(id);
        redirectAttributes.addFlashAttribute("mensagem", "Certidão excluída com sucesso.");
        return "redirect:/certidoes";
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public String handleNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", "Certidão não encontrada.");
        return "redirect:/certidoes";
    }
}
