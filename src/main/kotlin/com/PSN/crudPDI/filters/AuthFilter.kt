package com.PSN.crudPDI.filters

import com.PSN.crudPDI.Constants
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class AuthFilter: GenericFilterBean() {
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {

        val httpRequest = servletRequest as HttpServletRequest
        val httpResponse: HttpServletResponse = servletResponse as HttpServletResponse

        //validação da rota de login
        if (publicRoute(servletRequest)) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(servletRequest, servletResponse)
        }else
        {
            val authHeader: String = httpRequest.getHeader("Authorization")

            /*
            Condição que valida se existe algo no header. Após isso valida o valor sendo uma String Bearer e em seguida
            retorna o token gerado pela aplicação.
             */
            if(authHeader != null)
            {
                val authHeaderArr = authHeader.split("Bearer ")

                if(authHeaderArr.size > 1 && authHeaderArr[1] != null)
                {
                    val token: String = authHeaderArr[1]
                    try{
                           Jwts.parser().setSigningKey(Constants.API_SECRET_KEY)
                                .parseClaimsJws(token).body
                       // httpRequest.setAttribute("id", Integer.parseInt(claims.get("id").toString()))
                    }
                    catch (e: Exception)
                    {
                        httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "token invalido/expirado")
                        return
                    }
                }
                else
                {
                    httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Token de autenticação deve ser Bearer [token]")
                    return
                }
            } else{
                httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Token de autenticação deve ser gerado")
                return
            }

        }

        chain.doFilter(servletRequest, servletResponse)
    }

    fun publicRoute(request: HttpServletRequest): Boolean {
        if(request.requestURI.contains("/CrudPDI/login"))
        {
            return true

        }
        return false
    }
}