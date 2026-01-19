package com.plcoding.cryptotracker.settings.releases.data.remote

import com.plcoding.cryptotracker.settings.releases.data.remote.dto.MainGitDTO
import com.plcoding.cryptotracker.settings.releases.domain.MainGit

fun MainGitDTO.toMainGit(): MainGit {
    return MainGit(
        assets = assets,
        assets_url = assets_url,
        author = author,
        body = body,
        created_at = created_at,
        draft = draft,
        html_url = html_url,
        id = id,
        immutable = immutable,
        name = name,
        node_id = node_id,
        prerelease = prerelease,
        published_at = published_at,
        tag_name = tag_name,
        tarball_url = tarball_url,
        target_commitish = target_commitish,
        updated_at = updated_at,
        upload_url = upload_url,
        url = url,
        zipball_url = zipball_url,
    )
}